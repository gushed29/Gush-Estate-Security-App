<?php

declare(strict_types=1);

namespace GushSecurity;

/**
 * Lightweight Technology-Neutral REST API Router.
 * Maps clean REST routes without exposing internal file paths or .php extensions.
 */
final class Router
{
    private array $routes = [];

    public function get(string $path, callable $handler): void
    {
        $this->addRoute('GET', $path, $handler);
    }

    public function post(string $path, callable $handler): void
    {
        $this->addRoute('POST', $path, $handler);
    }

    public function put(string $path, callable $handler): void
    {
        $this->addRoute('PUT', $path, $handler);
    }

    public function delete(string $path, callable $handler): void
    {
        $this->addRoute('DELETE', $path, $handler);
    }

    private function addRoute(string $method, string $path, callable $handler): void
    {
        $this->routes[] = [
            'method' => strtoupper($method),
            'pattern' => $this->convertPathToRegex($path),
            'handler' => $handler,
        ];
    }

    public function dispatch(): void
    {
        $requestMethod = strtoupper($_SERVER['REQUEST_METHOD'] ?? 'GET');
        $requestUri = parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH);

        // Normalize trailing slashes
        $cleanUri = rtrim($requestUri, '/') ?: '/';

        // Handle CORS pre-flight
        if ($requestMethod === 'OPTIONS') {
            $this->handleCorsPreflight();
            return;
        }

        foreach ($this->routes as $route) {
            if ($route['method'] === $requestMethod && preg_match($route['pattern'], $cleanUri, $matches)) {
                $params = array_filter($matches, 'is_string', ARRAY_FILTER_USE_KEY);
                call_user_func($route['handler'], $params);
                return;
            }
        }

        // 404 Route Not Found
        Logger::warning("Route not found", ['method' => $requestMethod, 'uri' => $cleanUri]);
        Response::error('ROUTE_NOT_FOUND', "Endpoint {$requestMethod} {$cleanUri} does not exist", 404);
    }

    private function convertPathToRegex(string $path): string
    {
        $clean = rtrim($path, '/') ?: '/';
        $pattern = preg_replace('/\{([a-zA-Z0-9_]+)\}/', '(?P<$1>[a-zA-Z0-9_\-\.]+)', $clean);
        return '#^' . $pattern . '$#i';
    }

    private function handleCorsPreflight(): void
    {
        http_response_code(204);
        header('Access-Control-Allow-Origin: *');
        header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
        header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Gush-Timestamp, X-Gush-Request-Id, X-Gush-Signature, X-Estate-Id, Idempotency-Key');
        header('Access-Control-Max-Age: 86400');
        exit;
    }
}
