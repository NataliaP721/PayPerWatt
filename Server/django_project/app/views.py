from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from polls.models import PayPerWatt

# Create your views here.

from django.http import HttpResponse

def get(request):
        global password
        return HttpResponse(password)

@csrf_exempt
def post(request):
	change = PayPerWatt.objects.get(id=1)
        input = request.POST.get('data', '')
        s = input.split("\t")
        if s[0]=="passwordSave":
            change.password = s[1]
            change.save()
            return HttpResponse(PayPerWatt.objects.get(id=1).password)

     	elif s[0]=="passwordVerify":
            currentPassword = PayPerWatt.objects.get(id=1).password
            if s[1]==currentPassword:
                  return HttpResponse("true")
            else:
                  return HttpResponse("false")
        elif s[0]=="isInUse":
            isInUse = PayPerWatt.objects.get(id=1).isInUse
            return HttpResponse(isInUse)

        elif s[0]=="token":
            change.token = s[1]
            change.save()
            return HttpResponse("Received token")
        elif s[0]=="authorize":
            change.authorizedAmount = s[1]
            change.isInUse = "true"
            change.save()
            return HttpResponse(PayPerWatt.objects.get(id=1).authorizedAmount)
        elif s[0] == "stop_charging":
            change.chargeAmount = s[1]
            change.isInUse = "false"
            change.token = ""
            change.save()
            return HttpResponse("true")
        else:
        	return HttpResponse("Invalid input!")
