import re
import traceback
import requests
from parsel import Selector

FIND_ID_REGEX = re.compile("/([0-9]+)")


session = requests.Session()
session.trust_env = False
headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
}


def download(url, additional_headers={}):
    joined_headers = dict(headers)
    joined_headers.update(additional_headers)
    return session.get(url, headers=joined_headers).text


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
