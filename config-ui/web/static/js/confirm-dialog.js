
class ConfirmDialog extends React.Component{




    constructor(props){
        super(props);

        this.html = {};
        this.html.content = React.createRef();
        this.html.confirmBtn = React.createRef();
    }


    componentDidMount(){

        var h = this.html.content.current;
        var ok = this.html.confirmBtn;

        window.showConfirmDialog = function(settings){

            var p = new Promise( (resolve, reject)=>{

                if(settings){
                    if(settings.content){
                        h.innerHTML = settings.content;
                    }
                }

                var clone = ok.cloneNode(true);
                ok.parentNode.replaceChild(clone, ok); //a ninja way to remove all the event listeners.
                ok.addEventListener('click', (event)=>{
                    resolve(event);
                });




                jQuery('#confirmModal').modal();
            });

            return p;
        };
    }

    render(){
        return (
            <div id="confirmModal" className="modal fade"
                tabIndex="-1" role="dialog" aria-labelledby="confirmDialog-label" aria-hidden="true">
                <div className="modal-dialog modal-dialog-centered modal-sm " role="document">
                    <div className="modal-content">
                        <div className="modal-header" style={{height: '35px', paddingTop: '5px'}}>
                            <span className="modal-title" id="confirmDialog-label">Confirm</span>
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            <div ref={this.html.content} style={{marginBottom: '16px'}}>

                            </div>
                            <div style={{display: 'flex', justifyContent: 'center'}}>
                                <button className="btn btn-secondary" type="button" data-dismiss="modal" style={{marginRight: '5px'}}>Cancel</button>
                                <button onClick={this.save} ref={this.html.confirmBtn} className="btn btn-danger" type="button">Confirm</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}