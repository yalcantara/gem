;
if(typeof(window.utils) === 'undefined' || window.utils == null){
    window.utils = {};
}



utils.findAndDelete = function(arr, field, val){
    var ans = [];
    if(arr && arr.length > 0){
        for(var i =0; i < arr.length; i++){
            var other = arr[i];
            if(other){
                if(other[field]){
                    if(other[field] === val){
                        continue;
                    }
                    ans.push(other);
                }else{
                    ans.push(other);
                }
            }else{
                ans.push(other);
            }
        }
    }

    return ans;
};



utils.formatDate = function(unixTime){
    return moment(new Date(unixTime)).format('MMMM Do YYYY, h:mm:ss a');
};

utils.sortField = function(arr, field){
    if(arr){
        arr.sort((x, y) => {
            var a = x[field];
            var b = y[field];
            if (a > b) {
                return 1;
            }
            if (a < b) {
                return -1;

            } if (a === b) {
                return 0;
            }
        });
    }
};