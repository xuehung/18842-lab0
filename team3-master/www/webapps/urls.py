from django.conf.urls import patterns, include, url
from django.contrib import admin

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'webapps.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r'^$', 'car4go.views.home'),
    
    url(r'^share/',include('car4go.shareEventUrls')),
    url(r'^rent/',include('car4go.rentEventUrls')),
    



    url(r'^admin/', include(admin.site.urls)),
)

