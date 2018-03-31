from django.db import models

# Create your models here.

class PayPerWatt(models.Model):
	id = models.IntegerField(default = 0, primary_key=True)
	isInUse = models.CharField(max_length=5)
	password = models.CharField(max_length=200)
	token = models.CharField(max_length=200)
	authorizedAmount = models.IntegerField(default = 0)
	chargedAmount = models.FloatField(default = 0)