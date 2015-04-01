from django.shortcuts import render
from django.http import HttpResponse
from django.contrib.sessions import serializers
from django.core.serializers import serialize
from django.utils import timezone
from car4go.models import *
# Create your views here.



def home(request):
    return render(request, "car4go/index.html", {})


def listShareEvent(request):
    # share_event_text = serialize('json', ShareEvent.objects.all())
    #return HttpResponse(share_event_text, content_type='application/json')
    items = ShareEvent.objects.all()
    return render(request, "car4go/share-list.html", {"items": items})


def getShareEvent(request):
    print "bibibi"
    des = ""
    dep = ""
    items = ShareEvent.objects.all()
    if request.POST['destination']:
        des = request.POST['destination']
        items = ShareEvent.objects.filter(destination=des)
    if request.POST['departure']:
        dep = request.POST['departure']
        items = items.filter(departure=dep)
    return render(request, "car4go/share-list.html", {"items": items})


def postShareEvent(request):
    # errors = []
    # if not 'shareEvent' in request.POST or not request.POST['shareEvent']:
    # print 'if'
    #     errors.append('You must enter a share event to post.')
    # else:
    #     print 'else'
    #     new_share_event = ShareEvent(share_time=request.POST['share_time'], distance=request.POST['distance'],
    #                                  fee=request.POST['fee'], available_seats=request.POST['seats'],
    #                                  car_type=request.POST['car_type'],
    #                                  status=request.POST['status'], departure=request.POST['departure'],
    #                                  destination=request.POST['destination'], post_time=timezone.now())
    #     new_share_event.save()
    return render(request, "car4go/share-post.html", {})

#Attention: departure_time can't get from front end.
def postEventEntry(request):
    print 'in postEventEntry'
    print request.POST
    new_share_event = ShareEvent(departure=request.POST['departure'], destination=request.POST['destination'],
                                 departure_time='2014/3/23', return_time=request.POST.get('datetime'),
                                 fee=request.POST['fee'], available_seats=request.POST['available_seats'],
                                 car_type=request.POST['car_type'], is_active=True)

    new_share_event.save()
    # new_user_event = UserShareEvent(user=request.user, share_event=new_share_event, username=request.user.username,
    #                                 is_owner=request.user.username, credit_level=True, is_active=True)
    # new_user_event.save()
    response_text = serialize('json', ShareEvent.objects.all())
    return HttpResponse(response_text, content_type='application/json')




