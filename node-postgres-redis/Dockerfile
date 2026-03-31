FROM node:22-alpine AS base
WORKDIR /app
COPY package.json ./
RUN npm install --omit=dev
COPY src/ ./src/

FROM base AS final
ENV NODE_ENV=production
ENV PORT=3000
EXPOSE 3000
USER node
CMD ["node", "src/index.js"]
