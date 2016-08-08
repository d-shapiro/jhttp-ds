# jhttp-ds
A simple library for sending HTTP requests

# Usage

jhttp.HttpRequest contains static methods for creating requests. Every request has a send() method which sends the request and
returns its response.

## URL Parameters

When creating a request, you can have placeholders in the url surrounded by curly braces (e.g. http://{IP}:8020/example/{node}).
You can then replace the placeholders with values using the addUrlParam(String key, String value) method.

## Basic Authentication

The static method jhttp.HttpRequest.setDefaultLogin(String username, String password) will set the authentication used by http
requests.

## Main

The class jhttp.Main is an example of a command-line http client.
