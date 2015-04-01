from django.conf.urls import patterns, include, url
from django.contrib import admin

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'webapps.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r'^$', 'car4go.views.listShareEvent'),
    url(r'^list/','car4go.views.listShareEvent'),
    url(r'^post/','car4go.views.postShareEvent'),
    url(r'^get/','car4go.views.getShareEvent'),
    url(r'^postEntry/','car4go.views.postEventEntry'),
)
