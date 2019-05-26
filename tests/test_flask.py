from main import app
import pytest
import json


@pytest.fixture
def client():
    client = app.test_client()

    yield client


sites = [
    "/kwejk",
    "/jbzd",
    "/9gag",
    "/9gagnsfw",
    "/demotywatory",
    "/mistrzowie",
    "/anonimowe",
]


# This test could fail if the site changes it's schema or is not functional
@pytest.mark.parametrize("site", sites)
def test_sites(client, site):
    r = client.get(site)

    assert r.status == "200 OK"
    data = json.loads(r.data)

    assert len(data["memes"]) > 0
    assert data["next_page_url"] is not None

    r = client.get(data["next_page_url"])

    assert r.status == "200 OK"
    data = json.loads(r.data)

    assert len(data["memes"]) > 0
    assert data["next_page_url"] is not None
