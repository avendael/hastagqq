import datetime
import json

from tornado import ioloop
from tornado import web
from dal import ScopedSession
from models import Device, News
from sqlalchemy.orm.exc import NoResultFound

SUCCESS = "00000"
FAIL = "00001"


class DeviceRegistrationHandler(web.RequestHandler):
    def post(self):
        db = self.application.db
        data = json.loads(self.request.body.decode("utf8"))

        try:
            device = db.query(Device).filter(
                Device.gcm_id == data["gcm_id"]).one()
            response = {
                "message": "You are already registered",
                "response": FAIL}
            self.write(json.dumps(response))
        except NoResultFound:
            device = Device(gcm_id=data["gcm_id"])
            response = {
                "message": "Successfully registered",
                "response": FAIL
            }

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
                    if len(params) < 2 or len(params) > 2:
                        self.write(json.dumps(response))
                    else:
                        if news is None:
                            news = db.query(News).filter(
                                getattr(News, arg[0]) == arg[1])
                        else:
                            news.filter(getattr(News, arg[0] == arg[1]))
                        response = {
                            "response": SUCCESS,
                            "message": "Retrieved specific news item",
                            "news": {
                                "title": news.title,
                                "content": news.content,
                                "location": news.location,
                                "category": news.category,
                                "score": news.score
                            }
                        }
            except NoResultFound:
                response = {
                    "message": "No such item",
                    "response": FAIL
                }
            except AttributeError:
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
