# Memes API [![Build Status](https://travis-ci.org/pr0gramista/memes-api.svg?branch=master)](https://travis-ci.org/pr0gramista/memes-api) [![codecov](https://codecov.io/gh/pr0gramista/memes-api/branch/master/graph/badge.svg)](https://codecov.io/gh/pr0gramista/memes-api)

API for scrapping common meme sites.
Currently supports:
* [demotywatory.pl](http://demotywatory.pl)
* [kwejk.pl](http://kwejk.pl)
* [mistrzowie.org](http://mistrzowie.org)
* [jbzd.pl](https://jbzdy.pl)
* [9gag.com](http://9gag.com)

No sites to be supported, suggest one?

## API
`/`

Response: available sites
```
[
  "/demotywatory",
  "/kwejk",
  "/mistrzowie",
  "/thecodinglove",
  "/jbzd",
  "/9gag"
]
```

Then you can access them by accessing fe. `/kwejk`
```
//shortened response
{
  "memes": [
    {
      "title": "Czasy się zmieniają",
      "url": "https://kwejk.pl/obrazek/3387625/czasy-sie-zmieniaja.html",
      "view_url": "/kwejk/3387625",
      "author": {
        "name": "Torendil",
        "url": "https://kwejk.pl/uzytkownik/torendil"
      },
      "comment_count": 18,
      "content": {
        "contentType": "IMAGE",
        "url": "https://i1.kwejk.pl/k/obrazki/2019/05/lJUqdnyKqJf1Katl.jpg"
      },
      "points": 205,
      "tags": [
        {
          "name": "#obrazek",
          "url": "https://kwejk.pl/tag/obrazek"
        },
        {
          "name": "#humor",
          "url": "https://kwejk.pl/tag/humor"
        },
        {
          "name": "#mem",
          "url": "https://kwejk.pl/tag/mem"
        },
        {
          "name": "#true",
          "url": "https://kwejk.pl/tag/true"
        }
      ]
    }
  ],
  "next_page_url": "/kwejk/page/40878",
  "title": "Ministerstwo memów, zdjęć i innych śmiesznych obrazków - KWEJK.pl"
}
```
