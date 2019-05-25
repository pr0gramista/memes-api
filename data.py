class Page:
    def __init__(self, title, memes, next_page_url):
        self.title = title
        self.memes = memes
        self.next_page_url = next_page_url


class TextContent:
    def __init__(self, text):
        self.contentType = "TEXT"
        self.text = text


class VideoContent:
    def __init__(self, url):
        self.contentType = "VIDEO"
        self.url = url


class GalleryContent:
    def __init__(self, images):
        self.contentType = "GALLERY"
        self.urls = images


class ImageContent:
    def __init__(self, src):
        self.contentType = "IMAGE"
        self.url = src


class Tag:
    def __init__(self, name, url):
        self.name = name
        self.url = url


class Meme:
    def __init__(
        self, title, url, view_url, content, author, tags, points, comment_count
    ):
        self.title = title
        self.url = url
        self.view_url = view_url
        self.content = content
        self.tags = tags
        self.points = points
        self.comment_count = comment_count
        self.author = author


class Author:
    def __init__(self, name, url):
        self.name = name
        self.url = url
