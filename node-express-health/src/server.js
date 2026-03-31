import express from "express";

const app = express();

app.get("/health", (_req, res) => {
  res.json({ ok: true });
});

const port = Number(process.env.PORT) || 3333;
app.listen(port);
