;
if(typeof(window.utils) === 'undefined' || window.utils == null){
    window.utils = {};
}


utils.formatDate = function(unixTime){
    return moment(new Date(unixTime)).format('MMMM Do YYYY, h:mm:ss a');
}

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
}