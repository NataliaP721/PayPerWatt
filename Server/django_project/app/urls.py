from django.conf.urls import include, url

from . import views

urlpatterns = [
    url(r'^post/', views.post, name="post"),
    url(r'^', views.get, name='get'),
]