import time
import json

from tornado import ioloop
from tornado import web
from dal import ScopedSession
from models import Device, News
from sqlalchemy.orm.exc import NoResultFound

SUCCESS = '00000'
FAIL = '00001'


class DeviceRegistrationHandler(web.RequestHandler):
    def post(self):
        db = self.application.db
        data = json.loads(self.request.body.decode('utf8'))

        try:
            device = db.query(Device).filter(
                Device.gcm_id == data['gcm_id']).one()
            response = {
                'message': 'You are already registered',
                'response': FAIL}
            self.write(json.dumps(response))
        except NoResultFound:
            device = Device(gcm_id=['gcm_id'])
            response = {
                'message': 'Successfully registered',
                'response': FAIL
            }

            db.add(device)
            db.commit()
            db.close()

            self.write(json.dumps(response))


class NewsHandler(web.RequestHandler):
    def post(self):
        db = self.application.db
        data = json.loads(self.request.body.decode('utf8'))
        news = News(
            title=data['title'],
            content=data['content'],
            location=data['location'],
            tag=data['tag'],
            timestamp=time.stftime('%Y-%m-%d %H:%M:%S', time.gmtime())
        )
        response = {
            'message': 'Successfully created news',
            'response': SUCCESS
        }

        db.add(news)
        db.commit()
        db.close()

        self.write(json.dumps(response))

    def get(self):
        db = self.application.db
        data = json.loads(self.request.body.decode('utf8'))
        location = data['location']
        news_items = db.query(News).filter(News.location == location)
        response = {
            'news_items': news_items,
            'message': 'Successfully retrieved news',
            'response': SUCCESS
        }

        self.write(json.dumps(response))


class WebApi(web.Application):
    def __init__(self):
        handlers = [
            (r'/', DeviceRegistrationHandler),
        ]

        web.Application.__init__(self, handlers)

        self.db = ScopedSession()

application = WebApi()

if __name__ == "__main__":
    application.listen(8080)
    ioloop.IOLoop.instance().start()
