from django.db import models
from django.contrib.auth.models import User
import timedelta    

# Create your models here.
class University(models.Model):
        id = models.AutoField(primary_key=True)
	name = models.CharField(max_length=100)
	description = models.CharField(max_length=1000,blank=True)

#class User(models.Model):
#	university = models.ForeignKey(University)
#	first_name = models.CharField(max_length=20, blank=True)
#	last_name = models.CharField(max_length=20, blank=True)

class ShareEvent(models.Model):
    id = models.AutoField(primary_key=True)
    departure_time = models.CharField(max_length=50, blank=True, null=True)
    # duration = timedelta.fields.TimedeltaField(blank=True)
    return_time = models.CharField(max_length=50, blank=True, null=True)
    distance = models.IntegerField(max_length=10, blank=True, null=True)
    fee = models.IntegerField(null=True, blank=True)
    available_seats = models.IntegerField(null=True, blank=True)
    car_type = models.CharField(max_length=20, blank=True)
    is_active = models.BooleanField(default=False, blank=True)
    departure = models.CharField(max_length=50, blank=True)
    destination = models.CharField(max_length=50, blank=True)
    lat = models.DecimalField(max_digits=10, decimal_places=10, blank=True, null=True)
    lng = models.DecimalField(max_digits=10, decimal_places=10, blank=True, null=True)

    

class UserShareEvent(models.Model):
	user = models.ForeignKey(User, related_name='user')
	share_event = models.ForeignKey(ShareEvent, related_name = 'share_event')
	username = models.CharField(max_length=20, blank=True)
	is_owner = models.BooleanField(default=False, blank=True)
	credit_level = models.CharField(max_length=20, blank=True)
   	is_active = models.BooleanField(default=False, blank=True)
