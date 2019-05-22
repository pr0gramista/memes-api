from parsel import Selector
from utils import download, get_last_part_url, catch_errors
from data import VideoContent, ImageContent, Meme, Author, Tag
import json
import html

ROOT = "https://9gag.com"


def scrap(url):
    data = download(url)
    return parse(json.loads(data))


def parse(json):
    posts = json["data"]["posts"]
    memes = [catch_errors(parse_meme, post) for post in posts]
    return [meme for meme in memes if meme is not None]


def parse_meme(m):
    title = html.unescape(m["title"])
    url = m["url"]

    comment_count = int(m["commentsCount"])
    points = int(m["upVoteCount"]) - int(m["downVoteCount"])
    tags = [Tag(tag["key"], ROOT + tag["url"]) for tag in m["tags"]]

    content = None

    # Determine type
    t = m["type"]
    if t == "Animated":
        for k, v in m["images"].items():
            if "duration" in v:
                content = VideoContent(v["url"])
    else:
        src = m["images"]["image700"]["url"]
        content = ImageContent(src)

    return Meme(
        title,
        url,
        "/9gag/{}".format(get_last_part_url(url)),
        content,
        None,
        tags,
        points,
        comment_count,
    )
