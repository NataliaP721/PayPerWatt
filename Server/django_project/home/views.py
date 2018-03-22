from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse


def index(request):
    return HttpResponse("<html><header><title>PayPerWatt</title></header><body>PayPerWatt</body></html>")