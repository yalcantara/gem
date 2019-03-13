function formatDate(unixTime){
    return moment(new Date(unixTime)).format('MMMM Do YYYY, h:mm:ss a');
}