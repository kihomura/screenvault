FROM node:18 as build
WORKDIR /app

ENV ROLLUP_NO_NATIVE=true

COPY package*.json ./
RUN npm install

COPY . .
RUN npm install @rollup/rollup-linux-x64-gnu --no-save || true
RUN ROLLUP_NO_NATIVE=true npm run build

# Production stage
FROM nginx:1.25-alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 5173

CMD ["nginx", "-g", "daemon off;"]