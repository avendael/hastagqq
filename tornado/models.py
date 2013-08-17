from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.schema import Table
from sqlalchemy.orm import relationship


Base = declarative_base()
news_tags = Table(
    'news_tags', Base.metadata,
    Column('news_id', Integer, ForeignKey('news.id', ondelete='cascade')),
    Column('tag_id', Integer, ForeignKey('tags.id', ondelete='cascade')))


class News(Base):
    __tablename__ = 'news'

    id = Column(Integer, primary_key=True)
    title = Column(String())
    content = Column(String())
    location = Column(String())
    timestamp = Column(DateTime, nullable=False)
    tags = relationship("Tag", secondary=news_tags,
                               backref="news_articles",
                               cascade="all, delete")

    def __repr__(self):
        return "<News: %s -- %s>" % (self.title, self.content)


class Tag(Base):
    __tablename__ = 'tags'

    id = Column(Integer, primary_key=True)
    name = Column(String())

    def __repr__(self):
        return "<Tag: %s>" % self.name


class Device(Base):
    __tablename__ = 'devices'
    id = Column(Integer, primary_key=True)
    gcm_id = Column(String(1024), nullable=False, unique=True, index=True)

    def __repr__(self):
        return "<Device: %s>" % self.gcm_id
