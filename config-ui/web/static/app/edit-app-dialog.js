class EditAppDialog extends React.Component { 
        
    state =  {
        appName: '',
        app: null
    };

    props = {};

    form = null;
    errorHolder = null;
    
    constructor(props) { 
        super(props); 

        this.props = props;

        this.html = {};
        this.html.modal = React.createRef();
        this.html.form = React.createRef();
        this.html.name = React.createRef();
        this.html.oldName = React.createRef();
        this.html.msg = React.createRef();
        this.html.label = React.createRef();
        this.html.errorHolder = React.createRef();

        this.save = this.save.bind(this);

    } 

    setRecord(record){
        this.state.appName = record.name;
        this.state.app = record;
        this.setState({appName: record.name});
    }

    setOkCallback(func){
        this.okCallback = func;
    }

    beforeShow(){
        var msg = this.html.msg.current;
        var nameInput = this.html.name.current

        nameInput.value = this.state.appName;
        nameInput.classList.remove('is-invalid');
        msg.style.display = 'none';
        this.errorHolder.css('display', 'none');
    }

    validate(){
        
        var msg = this.html.msg.current;
        var nameInput = this.html.name.current;
        var name = this.html.name.current.value;

        if(name == null || name == '' || name.trim() == ''){
            nameInput.classList.add('is-invalid');
            msg.className = 'invalid-feedback';
            msg.style.display = 'block';
            msg.innerHTML = 'The app name is required.';
            nameInput.focus();
            
            return false;
        }

        nameInput.classList.remove('is-invalid');
        msg.style.display = 'none';

        return true;
    }
    
    save(event) {
        
        if(this.validate() == false){
            return;
        }

        var name = this.html.name.current.value;
        var label = this.html.label.current.value;
        
        const id = this.state.app.id;
        var app = {name: name, label: label};
        var self = this;
        rest.putAndGet('/rest/config/apps/' + id, app).then(function(res){
            self.errorHolder.css('display', 'none');
            if(self.okCallback){
                self.okCallback(res);
            }
        }).catch(function(res){
            var msg = 'There was an error in the system. Please try again later.';
            if(res.jqXHR.status === 409 || res.jqXHR.status == 400){
                msg = res.jqXHR.responseText;
            }
            self.errorHolder.css('display', 'block');
            self.errorHolder.html(msg);
        });
    } 

    handleKeyPress(event){
        var key = event.key;
        var which = event.which;
        if(key == 'Enter' || which == 13){
            this.save();
        }
    }
    
    componentDidMount() {
        this.form = jQuery(this.html.form.current);
        this.errorHolder = jQuery(this.html.errorHolder.current);

        const self = this;
        var nameInput = jQuery(this.html.name.current);

        this.form.find('input, button').on('keypress', (event)=>{self.handleKeyPress(event)});

        //let's add the events later
        setTimeout(function(){
            jQuery(self.html.modal.current).on('shown.bs.modal', function(){
                nameInput.focus();
            });

            jQuery(self.html.modal.current).on('show.bs.modal', function(){
                self.beforeShow();
            });
        },100);
    }
    
    render() { 
        return (
            <div id={this.props.id} ref={this.html.modal} className="modal fade"
                tabIndex="-1" role="dialog" data-backdrop="static" aria-hidden="true">
                <div className="modal-dialog modal-dialog-centered modal-sm" role="document">
                    <div className="modal-content">
                        <div className="modal-header" style={{height: '35px', paddingTop: '5px'}}>
                            <span className="modal-title">Edit App</span>
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            <form ref={this.html.form} style={{marginBottom: '5px'}}>
                                <div className="form-group">
                                    <label style={{width: '100%'}}>
                                        App: <input ref={this.html.name} value={this.state.appName} className="form-control" type="text" disabled/>
                                    </label>
                                </div>
                                <div className="form-group">
                                    <label style={{width: '100%'}}>
                                        Name: <input ref={this.html.name} className="form-control" type="text"/>
                                    </label>
                                    <div ref={this.html.msg} style={{display: 'none'}}></div>
                                </div>
                                <div className="form-group">
                                    <label style={{width: '100%'}}>
                                        Label:  <input ref={this.html.label} className="form-control" type="text"/>
                                    </label>
                                </div>
                                <div ref={this.html.errorHolder} className="alert alert-danger" style={{display: 'none'}}></div>
                                <div style={{display: 'flex', justifyContent: 'flex-end'}}>
                                    <button type="button" className="btn btn-secondary" data-dismiss="modal" style={{marginRight: '5px'}}>Close</button>
                                    <button onClick={this.save} type="button" className="btn btn-success">Update</button>
                                </div>
                            </form> 
                        </div>
                    </div>
                </div>
            </div>
        ); 
    } 
} 