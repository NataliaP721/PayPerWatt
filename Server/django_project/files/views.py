from django.shortcuts import render

# Create your views here.

import os
from django.conf import settings
from django.http import HttpResponse

def download(request):
    path = 'app-debug.apk'
    file_path = os.path.join(settings.MEDIA_ROOT, path)
    if os.path.exists(file_path):
        with open(file_path, 'rb') as fh:
            response = HttpResponse(fh.read(), content_type="application/vnd.android.package-archive")
            response['Content-Disposition'] = 'inline; filename=' + os.path.basename(file_path)
            return response
    raise Http404
