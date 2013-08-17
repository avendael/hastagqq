import sqlalchemy
import sqlite3

from sqlalchemy import create_engine
from sqlalchemy.interfaces import PoolListener
from sqlalchemy.orm import sessionmaker, scoped_session

from models import Base


class DBPoolListener(PoolListener):
    def __init__(self):
        self.retried = False

    def checkout(self, dbapi_con, con_record, con_proxy):
        SQLITE_SELECT_NOW = "select date('now')"
        try:
            dbapi_con.cursor().execute(SQLITE_SELECT_NOW)
        except sqlite3.OperationalError:
            if self.retried:
                self.retried = False
                raise

            self.retried = True
            raise sqlalchemy.exc.DisconnectionError

sqlite_engine = create_engine('sqlite:///app.db',
                              echo=True,
                              listeners=[DBPoolListener()])
session_factory = sessionmaker(bind=sqlite_engine)
ScopedSession = scoped_session(session_factory)

Base.metadata.create_all(sqlite_engine)
