const express = require("express");
const app = express();

const PORT = process.env.PORT || 3000;
const APP_ENV = process.env.APP_ENV || "development";

app.get("/", (req, res) => {
  res.json({
    name: "demo-node-api",
    version: "1.0.0",
    env: APP_ENV,
    message: "Deployed by Chakra 🚀🚀🚀",
  });
});

app.get("/health", (req, res) => {
  res.json({ status: "ok", uptime: process.uptime() });
});

app.get("/items", (req, res) => {
  res.json({
    items: [
      { id: 1, name: "Widget A" },
      { id: 2, name: "Widget B" },
      { id: 3, name: "Widget C" },
    ],
  });
});

app.listen(PORT, () => {
  console.log(`demo-node-api listening on port ${PORT} [${APP_ENV}]`);
});
