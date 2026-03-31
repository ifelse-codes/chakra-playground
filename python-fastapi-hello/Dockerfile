FROM python:3.12-slim

WORKDIR /app

RUN useradd -u 1000 -m appuser

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY main.py .

ENV PORT=3000

EXPOSE 3000

USER 1000:1000

CMD ["python", "main.py"]
