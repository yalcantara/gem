
class ConfirmDialog extends React.Component{


    confirmCallback = null;
    state = {
        title: 'Confirm'
    };

    constructor(props){
        super(props);

        this.html = {};
        this.html.content = React.createRef();
        this.html.confirmBtn = React.createRef();
    }

    hide = ()=>{
        jQuery('#confirmModal').modal('hide');
    }

    confirm = ()=>{
        if(this.confirmCallback){
            this.confirmCallback();
            this.hide();
        }
    }

    componentDidMount(){

        var self = this;
        var h = this.html.content.current;
      

        window.showConfirmDialog = function(settings){

            var p = new Promise( (resolve, reject)=>{

                if(settings){
                    if(settings.content){
                        h.innerHTML = settings.content;
                    }

                    if(settings.title){
                        self.setState({title: settings.title});
                    }
                }
                self.confirmCallback = resolve;


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
                            <span className="modal-title" id="confirmDialog-label">{this.state.title}</span>
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            <div ref={this.html.content} style={{marginBottom: '16px'}}>

                            </div>
                            <div style={{display: 'flex', justifyContent: 'center'}}>
                                <button className="btn btn-secondary" type="button" data-dismiss="modal" style={{marginRight: '5px'}}>Cancel</button>
                                <button onClick={this.confirm} ref={this.html.confirmBtn} className="btn btn-danger" type="button">Confirm</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}