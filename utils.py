import httplib2
import re
import traceback
from parsel import Selector

FIND_ID_REGEX = re.compile("/([0-9]+)")

http = httplib2.Http()


def download(url):
    (resp_headers, content) = http.request(url, "GET")
    return content.decode("utf-8")


def download_multiple(urls):
    return [download(url) for url in urls]


def remove_big_whitespaces(t):
    return t.replace("  ", "").replace("\n", "")


def remove_big_whitespaces_selector(selector):
    return Selector(text=remove_big_whitespaces(selector.get()))


def find_id_in_url(url):
    result = FIND_ID_REGEX.search(url)
    if result is not None:
        return result[1]
    return None


def reverse(s):
    return s[::-1]


def get_last_part_url(url):
    s = reverse(url)
    for index, character in enumerate(s):
        if character == "/":
            return reverse(s[0:index])
    return None


def catch_errors(f, data):
    try:
        return f(data)
    except Exception:
        print(traceback.print_exc())
    return None
