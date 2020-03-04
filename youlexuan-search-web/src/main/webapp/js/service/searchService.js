app.service('searchService',function ($http) {
    this.search=function(map){
        return $http.post('../itemsearch/search.do',map);
    }

})