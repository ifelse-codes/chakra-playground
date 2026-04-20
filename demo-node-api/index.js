const express = require("express");
const app = express();

const PORT = process.env.PORT || 3000;
const APP_ENV = process.env.APP_ENV || "development";

// Structured logging middleware
app.use((req, res, next) => {
  const startTime = Date.now();

  res.on("finish", () => {
    const duration = Date.now() - startTime;
    const logEntry = {
      time: new Date().toISOString(),
      method: req.method,
      uri: req.originalUrl || req.url,
      status: res.statusCode,
      latency_ms: duration,
      remote_ip: req.ip || req.connection.remoteAddress,
      user_agent: req.get("user-agent") || "",
      host: req.get("host") || "",
      message: `${req.method} ${req.originalUrl || req.url} - ${res.statusCode} (${duration}ms)`
    };
    console.log(JSON.stringify(logEntry));
  });

  next();
});

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
