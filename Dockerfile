FROM python:3-alpine

WORKDIR /app

COPY requirements.txt ./

RUN apk add --update --no-cache py3-lxml libxslt-dev g++ python-dev

RUN pip install --no-cache-dir -r requirements.txt
RUN pip install gunicorn

COPY . .

EXPOSE 80

CMD [ "gunicorn", "-b", "0.0.0.0:80", "-w", "4", "main:app" ]