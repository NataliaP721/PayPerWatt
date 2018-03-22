from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt

# Create your views here.

from django.http import HttpResponse

# Globals
isInUse = "false"
password = "init"
token = ""
authorizedAmount = 0
chargeAmount = 0

def get(request):
        global password
        return HttpResponse(password)


@csrf_exempt
def post(request):
	global password
        input = request.POST.get('data', '')
        s = input.split("\t")
        if s[0]=="passwordSave":
            password = s[1]
            return HttpResponse(s[1])
        elif s[0]=="passwordVerify":
            if s[1]==password:
                  return HttpResponse("true")
            else:
                  return HttpResponse("false")
        elif s[0]=="isInUse":
            return HttpResponse(isInUse)
        return HttpResponse("Never went into if statements")
