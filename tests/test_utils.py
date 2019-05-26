from utils import *


def test_remove_big_whitespaces():
    assert remove_big_whitespaces("Hey John\nHow are you") == "Hey JohnHow are you"
    assert remove_big_whitespaces("Hey John\n  How are   you") == "Hey JohnHow are you"


def test_find_id_in_url():
    assert find_id_in_url("https://test.test/422525/") == "422525"
    assert find_id_in_url("https://test.test/page/422525/") == "422525"
    assert find_id_in_url("https://test.test/page/422525&id=2402") == "422525"


def test_get_last_part_url():
    assert get_last_part_url("https://test.test/gk3tH3") == "gk3tH3"
    assert get_last_part_url("wow") == None
