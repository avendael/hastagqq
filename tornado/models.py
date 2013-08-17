from sqlalchemy import Column, Integer, String, DateTime
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()


class Device(Base):
    __tablename__ = 'devices'
    id = Column(Integer, primary_key=True)
    gcm_id = Column(String(1024), nullable=False, unique=True, index=True)
    location = Column(String(), default="The Dark Void")

    def __repr__(self):
        return "<Device: %s>" % self.gcm_id


class News(Base):
    __tablename__ = 'news'

    id = Column(Integer, primary_key=True)
    title = Column(String())
    content = Column(String())
    location = Column(String())
    timestamp = Column(DateTime, nullable=False)
    category = Column(String())
    score = Column(Integer)

    def __repr__(self):
        return "<News: %s -- %s>" % (self.title, self.category)
