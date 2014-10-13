//= require_self <% def domainList = domainProperties.findAll{ it.domainClass }.collect { "${it.type.name - (group + '.')}" } %>
//= require controllers
//= require services <%= domainList.collect { '\n//= require /' + getModulePath(formatModuleName(rootModule + '.' + it)) + '/services' }.join("\n")  %>
//= require_tree /${modulePath}/templates/
<% def generateResolveProperty = { item -> """
				${item[0].toLowerCase() + item.substring(1)}List: function(${item}Resource) {
					return ${item}Resource.list();
				}	
"""
}
%>
'use strict';
angular.module('${fullModuleName}', [
'grails', 
'${fullModuleName}.controllers', <%= domainList.collect { "\n'${formatModuleName(rootModule + '.' + it)}.services'," }.join("\n") %>
'${fullModuleName}.services'
])
.value('defaultCrudResource', '${defaultResource}')
.config(function(<%='\\$routeProvider'%>) {
<%='\\$routeProvider'%>
        .when('/', {
            controller: 'ListCtrl as ctrl',
            templateUrl: 'list.html',
            resolve: {
                ${moduleName}List: function(<%='\\$route'%>, ${defaultResource}) {
                    var params = <%='\\$route'%>.current.params;
                    return ${defaultResource}.list(params);
                }<%= (domainList ? ',' : '' ) + domainList.collect{ generateResolveProperty(it) }.join(', ') %> 
            }
        })
        .when('/create', {
            controller: 'CreateEditCtrl as ctrl',
            templateUrl: 'create-edit.html',
            resolve: {
                ${moduleName}: function(${defaultResource}) {
                    return ${defaultResource}.create();
                }<%= (domainList ? ',' : '' ) + domainList.collect{ generateResolveProperty(it) }.join(', ') %> 
            }
        })
        .when('/edit/:id', {
            controller: 'CreateEditCtrl as ctrl',
            templateUrl: 'create-edit.html',
            resolve: {
                ${moduleName}: function(<%='\\$route'%>, ${defaultResource}) {
                    var id = <%='\\$route'%>.current.params.id;
                    return ${defaultResource}.get(id);
                }<%= (domainList ? ',' : '' ) + domainList.collect{ generateResolveProperty(it) }.join(', ') %> 
            }
        })
        .when('/show/:id', {
            controller: 'ShowCtrl as ctrl',
            templateUrl: 'show.html',
            resolve: {
                ${moduleName}: function(<%='\\$route'%>, ${defaultResource}) {
                    var id = <%='\\$route'%>.current.params.id;
                    return ${defaultResource}.get(id);
                }
            }
        })
        .otherwise({redirectTo: '/'});
});
