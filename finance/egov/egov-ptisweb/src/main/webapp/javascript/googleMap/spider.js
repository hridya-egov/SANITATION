#-------------------------------------------------------------------------------
# /**
#  * eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#  */
#-------------------------------------------------------------------------------
(function(){
var h=null;
(function(){function m(m,j){return function(){return m.apply(j,arguments)}}var s,t=Array.prototype.slice;if(((s=this.google)!=h?s.maps:void 0)!=h)this.SameCoordOverlayMarker=function(){function o(a,d){var b,e,f,g;this.map=a;this.g=d!=h?d:{};this.n=new this.constructor.c(this.map);this.j();this.b={};g=["click","zoom_changed","maptypeid_changed"];for(e=0,f=g.length;e<f;e++)b=g[e],j.addListener(this.map,b,m(function(){return this.unspiderfy()},this))}var j,p,q,k,n,c,r;c=o.prototype;c.VERSION="0.2.3";
p=google.maps;j=p.event;n=p.MapTypeId;r=Math.PI*2;c.nearbyDistance=20;c.circleSpiralSwitchover=9;c.circleFootSeparation=23;c.circleStartAngle=r/12;c.spiralFootSeparation=26;c.spiralLengthStart=11;c.spiralLengthFactor=4;c.spiderfiedZIndex=1E3;c.usualLegZIndex=10;c.highlightedLegZIndex=20;c.legWeight=1.5;c.legColors={usual:{},highlighted:{}};k=c.legColors.usual;q=c.legColors.highlighted;k[n.HYBRID]=k[n.SATELLITE]="#fff";q[n.HYBRID]=q[n.SATELLITE]="#f00";k[n.TERRAIN]=k[n.ROADMAP]="#444";q[n.TERRAIN]=
q[n.ROADMAP]="#f00";c.j=function(){this.a=[];this.f=[]};c.addMarker=function(a){var d;d=[j.addListener(a,"click",m(function(){return this.C(a)},this))];this.g.markersWontHide||d.push(j.addListener(a,"visible_changed",m(function(){return this.l(a,false)},this)));this.g.markersWontMove||d.push(j.addListener(a,"position_changed",m(function(){return this.l(a,true)},this)));this.f.push(d);this.a.push(a);return this};c.l=function(a,d){if(a._omsData!=h&&(d||!a.getVisible())&&!(this.q!=h||this.r!=h))return this.G(d?
a:h)};c.getMarkers=function(){return this.a.slice(0,this.a.length)};c.removeMarker=function(a){var d,b,e,f;a._omsData!=h&&this.unspiderfy();a=this.i(this.a,a);if(!(a<0)){b=this.f.splice(a,1)[0];for(e=0,f=b.length;e<f;e++)d=b[e],j.removeListener(d);this.a.splice(a,1);return this}};c.clearMarkers=function(){var a,d,b,e,f,g,c;this.unspiderfy();c=this.f;for(b=0,f=c.length;b<f;b++){d=c[b];for(e=0,g=d.length;e<g;e++)a=d[e],j.removeListener(a)}this.j();return this};c.addListener=function(a,d){var b,e;((e=
(b=this.b)[a])!=h?e:b[a]=[]).push(d);return this};c.removeListener=function(a,d){var b;b=this.i(this.b[a],d);b<0||this.b[a].splice(b,1);return this};c.clearListeners=function(a){this.b[a]=[];return this};c.trigger=function(){var g;var a,d,b,e,f,c;d=arguments[0];a=2<=arguments.length?t.call(arguments,1):[];g=(b=this.b[d])!=h?b:[],d=g;c=[];for(e=0,f=d.length;e<f;e++)b=d[e],c.push(b.apply(h,a));return c};c.s=function(a,d){var b,e,f,c,i;c=this.circleFootSeparation*(2+a)/r;e=r/a;i=[];for(f=0;0<=a?f<a:
f>a;0<=a?f++:f--)b=this.circleStartAngle+f*e,i.push(new p.Point(d.x+c*Math.cos(b),d.y+c*Math.sin(b)));return i};c.t=function(a,d){var b,e,f,c,i;f=this.spiralLengthStart;b=0;i=[];for(e=0;0<=a?e<a:e>a;0<=a?e++:e--)b+=this.spiralFootSeparation/f+e*5.0E-4,c=new p.Point(d.x+f*Math.cos(b),d.y+f*Math.sin(b)),f+=r*this.spiralLengthFactor/b,i.push(c);return i};c.C=function(a){var d,b,e,c,g,i,j,l,m;d=a._omsData!=h;(!d||!this.g.keepSpiderfied)&&this.unspiderfy();if(d)return this.trigger("click",a);else{c=[];
g=[];i=this.nearbyDistance*this.nearbyDistance;e=this.k(a.position);m=this.a;for(j=0,l=m.length;j<l;j++)d=m[j],d.getVisible()&&d.map!=h&&(b=this.k(d.position),this.o(b,e)<i?c.push({w:d,m:b}):g.push(d));return c.length===1?this.trigger("click",a):this.D(c,g)}};c.v=function(a){return{d:m(function(){return a._omsData.e.setOptions({strokeColor:this.legColors.highlighted[this.map.mapTypeId],zIndex:this.highlightedLegZIndex})},this),h:m(function(){return a._omsData.e.setOptions({strokeColor:this.legColors.usual[this.map.mapTypeId],
zIndex:this.usualLegZIndex})},this)}};c.D=function(a,d){var b,c,f,g,i,n,l,o,q,k;this.q=true;k=a.length;b=this.A(function(){var b,d,c;c=[];for(b=0,d=a.length;b<d;b++)o=a[b],c.push(o.m);return c}());g=k>=this.circleSpiralSwitchover?this.t(k,b).reverse():this.s(k,b);b=function(){var b,d,k;k=[];for(b=0,d=g.length;b<d;b++){f=g[b];c=this.B(f);q=this.z(a,m(function(a){return this.o(a.m,f)},this));l=q.w;n=new p.Polyline({map:this.map,path:[l.position,c],strokeColor:this.legColors.usual[this.map.mapTypeId],
strokeWeight:this.legWeight,zIndex:this.usualLegZIndex});l._omsData={F:l.position,e:n};if(this.legColors.highlighted[this.map.mapTypeId]!==this.legColors.usual[this.map.mapTypeId])i=this.v(l),l._omsData.u={d:j.addListener(l,"mouseover",i.d),h:j.addListener(l,"mouseout",i.h)};l.setPosition(c);l.setZIndex(Math.round(this.spiderfiedZIndex+f.y));k.push(l)}return k}.call(this);delete this.q;this.p=true;return this.trigger("spiderfy",b,d)};c.unspiderfy=function(a){var d,b,c,f,g,i,k;a==h&&(a=h);if(this.p!=
h){this.r=true;f=[];c=[];k=this.a;for(g=0,i=k.length;g<i;g++)b=k[g],b._omsData!=h?(b._omsData.e.setMap(h),b!==a&&b.setPosition(b._omsData.F),b.setZIndex(h),d=b._omsData.u,d!=h&&(j.removeListener(d.d),j.removeListener(d.h)),delete b._omsData,f.push(b)):c.push(b);delete this.r;delete this.p;this.trigger("unspiderfy",f,c);return this}};c.o=function(a,c){var b,e;b=a.x-c.x;e=a.y-c.y;return b*b+e*e};c.A=function(a){var c,b,e,f,g;b=e=0;for(f=0,g=a.length;f<g;f++)c=a[f],b+=c.x,e+=c.y;a=a.length;return new p.Point(b/
a,e/a)};c.k=function(a){return this.n.getProjection().fromLatLngToDivPixel(a)};c.B=function(a){return this.n.getProjection().fromDivPixelToLatLng(a)};c.z=function(a,c){var b,e,f,g,i;for(f=0,i=a.length;f<i;f++)if(g=a[f],g=c(g),!(typeof b!=="undefined"&&b!==h)||g<e)e=g,b=f;return a.splice(b,1)[0]};c.i=function(a,c){var b,e,f;if(a.indexOf!=h)return a.indexOf(c);for(b=0,f=a.length;b<f;b++)if(e=a[b],e===c)return b;return-1};o.c=function(a){return this.setMap(a)};o.c.prototype=new p.OverlayView;o.c.prototype.draw=
function(){};return o}()}).call(this);}).call(this);
