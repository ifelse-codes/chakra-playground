import express from "express";
import { S3Client } from "@aws-sdk/client-s3";
import Redis from "ioredis";
import pg from "pg";

const app = express();

// Lazy-init infra clients — connection failures don't crash the process
const s3 = new S3Client({});
const redis = new Redis(process.env.REDIS_URL || "redis://localhost:6379", {
  lazyConnect: true,
  enableOfflineQueue: false,
});
const pool = new pg.Pool({
  connectionString: process.env.DATABASE_URL || "postgres://localhost:5432/app",
});

// Suppress unhandled connection errors from crashing the process
redis.on("error", () => {});
pool.on("error", () => {});

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

app.get("/health", (_req, res) => {
  res.json({
    ok: true,
    infra: {
      postgres: !!process.env.DATABASE_URL,
      redis: !!process.env.REDIS_URL,
      s3: !!process.env.S3_BUCKET,
    },
  });
});

const port = Number(process.env.PORT) || 3000;
app.listen(port, () => {
  console.log(`node-postgres-redis listening on :${port}`);
  console.log({ s3, redis: redis.status, pool });
});
