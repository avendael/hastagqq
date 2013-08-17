import datetime
import json
import time

from tornado import ioloop
from tornado import web
from dal import ScopedSession
from models import Device, News
from gcm import GCM
from sqlalchemy.orm.exc import NoResultFound

SUCCESS = "00000"
FAIL = "00001"
gcm = GCM("AIzaSyDPVAnupkRfN4TMNKifHsjaKzPbSFMSz8M")


def send_gcm(db, location, message, gcm_id=None):
    clients = [
        device.gcm_id
        for device in db.query(Device).filter(Device.location == location)
    ] if gcm_id is None and gcm_id == '' else [gcm_id]

    gcm.json_request(registration_ids=clients, data=message)


class DeviceRegistrationHandler(web.RequestHandler):
    def post(self):
        db = self.application.db
        data = json.loads(self.request.body.decode("utf8"))
        location = "location" in data and data["location"] is not None\
                   and data["location"] != ''\
                   and data["location"] or "The Dark Void"
        try:
            device = db.query(Device).filter(
                Device.gcm_id == data["gcm_id"]).one()
            device.location = location
            response = {
                "message": "Device location updated",
                "response": SUCCESS}
        except NoResultFound:
            device = Device(gcm_id=data["gcm_id"], location=location)
            response = {
                "message": "Successfully registered",
                "response": SUCCESS
            }
        gcm_message = {
            'message': "New news item in your location.",
            'timestamp': time.strftime('%Y-%m-%d %H:%M:%S', time.gmtime())
        }

        db.query(News).filter(News.location == location).count() > 0\
            and send_gcm(db, location, gcm_message, gcm_id=device.gcm_id)
        db.add(device)
        db.commit()
        db.close()

        self.write(json.dumps(response))


class NewsHandler(web.RequestHandler):
    def post(self, query_params=None):
        db = self.application.db
        data = json.loads(self.request.body.decode("utf8"))
        news = News(
            title=data["title"],
            content=data["content"],
            location=data["location"],
            category=data["category"],
            timestamp=datetime.datetime.today()
        )
        response = {
            "message": "Successfully created news",
            "response": SUCCESS
        }
        gcm_message = {
            'message': "New news item in your location.",
            'timestamp': time.strftime('%Y-%m-%d %H:%M:%S', time.gmtime())
        }

        send_gcm(db, news.location, gcm_message)
        db.add(news)
        db.commit()
        db.close()

        self.write(json.dumps(response))

    def get(self, query_params=None):
        db = self.application.db
        response = {
            "response": FAIL,
            "message": "Invalid query"
        }

        if query_params is None or query_params == "":
            location = self.request.headers["device_location"]\
                       if "device_location" in self.request.headers else None

            if location is None or location == "":
                self.write(json.dumps(response))
            else:
                news_items = db.query(News).filter(News.location == location)
                json_news_items = [{
                    "title": news.title,
                    "content": news.content,
                    "location": news.location,
                    "category": news.category,
                    "score": news.score
                } for news in news_items]
                response = {
                    "news_items": json_news_items,
                    "message": "Successfully retrieved news",
                    "response": SUCCESS
                }
        else:
            try:
                params = query_params.split("&")
                news = None

                for param in params:
                    arg = param.split("=")
                    if len(arg) < 2 or len(arg) > 2:
                        self.write(json.dumps(response))
                    else:
                        if news is None:
                            news = db.query(News).filter(
                                getattr(News, arg[0]) == arg[1])
                        else:
                            news.filter(getattr(News, arg[0]) == arg[1])

                response = {
                    "response": SUCCESS,
                    "message": "Retrieved news items",
                    "news_items": [{
                        "title": item.title,
                        "content": item.content,
                        "location": item.location,
                        "category": item.category,
                        "score": item.score
                    } for item in news]
                }
            except NoResultFound:
                response = {
                    "message": "No such item",
                    "response": FAIL
                }
            except AttributeError as e:
                response = {
                    "response": FAIL,
                    "message": str(e)
                }
                self.write(json.dumps(response))

        self.write(json.dumps(response))


class WebApi(web.Application):
    def __init__(self):
        handlers = [
            (r"/device/?", DeviceRegistrationHandler),
            (r'/news/?([a-zA-Z0-9=&"]*)/?', NewsHandler),
        ]

        web.Application.__init__(self, handlers)

        self.db = ScopedSession()

application = WebApi()

if __name__ == "__main__":
    application.listen(8080)
    ioloop.IOLoop.instance().start()
