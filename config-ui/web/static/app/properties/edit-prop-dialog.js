
class EditPropDialog extends React.Component{


    crt = {};

    constructor(props){
        super(props);
    }

    show(record){
        this.crt = record;
        jQuery(this.refs.modal).modal('show');
    }




    validate(){
        var input = this.refs.nameInput;
        var msg = this.refs.nameMsg;
        return validator.required(input, msg, 'The name is required.');
    }


    save(){
        if(this.validate() == false){
            return;
        }

        var name = this.refs.nameInput.value.trim();
        var label = this.refs.labelInput.value.trim();

        var ent = {name: name, label: label};
        
        var self = this;
        var app = this.props.crtApp.name;
        const id = this.crt._id;

        rest.putAndGet('/rest/config/apps/' + app +'/properties/' + id, ent).then(function(res){
            self.refs.nameMsg.style.display = 'none';
            jQuery(self.refs.modal).modal('hide');
            self.props.updateHandler(res.data);
        }).catch(function(res){
            var msg = 'There was an error in the system. Please try again later.';
            if(res && res.appMsg){
                msg = res.appMsg;
            }
            self.refs.errorHolder.style.display = 'block';
            self.refs.errorHolder.innerHTML = msg;
        });
    }

    handleKeyPress(event){
        var key = event.key;
        var which = event.which;
        if(key == 'Enter' || which == 13){
            this.save();
        }
    }

    componentDidMount(){
        var self = this;

        var form = this.refs.form;
        var prevInput = this.refs.prevInput;
        var nameInput = this.refs.nameInput;
        var nameMsg = this.refs.nameMsg;

        var labelInput = this.refs.labelInput;
        
        
        
        jQuery(form).find('input, button').on('keypress', (event)=>{self.handleKeyPress(event)});
        
        jQuery(this.refs.modal).on('show.bs.modal', function(){
            validator.clear(nameInput, nameMsg);
            
            var crt = self.crt;
            prevInput.value = crt.name;
            nameInput.value = crt.name;
            labelInput.value = crt.label;
            
            self.refs.errorHolder.style.display = 'none';
        });

        jQuery(this.refs.modal).on('shown.bs.modal', function(){
            nameInput.focus();
        });
    }

    render(){
        return (
            <div ref="modal" className="modal fade"
                tabIndex="-1" role="dialog" data-backdrop="static" aria-hidden="true">
                <div className="modal-dialog modal-dialog-centered modal-sm" role="document">
                    <div className="modal-content">
                        <div className="modal-header" style={{height: '35px', paddingTop: '5px'}}>
                            <span className="modal-title">Edit Property</span>
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            <form ref="form" style={{marginBottom: '5px'}}>
                                <div className="form-group">
                                    <label style={{width: '100%'}}>
                                        Property: <input ref="prevInput" className="form-control" type="text" disabled/>
                                    </label>
                                </div>
                                <div className="form-group">
                                    <label style={{width: '100%'}}>
                                        Name: <input ref="nameInput" className="form-control" type="text"/>
                                    </label>
                                    <div ref="nameMsg" style={{display: 'none'}}></div>
                                </div>
                                <div className="form-group">
                                    <label style={{width: '100%'}}>
                                        Label:  <input ref="labelInput" className="form-control" type="text"/>
                                    </label>
                                </div>
                                <div ref="errorHolder" className="alert alert-danger" style={{display: 'none'}}></div>
                                <div style={{display: 'flex', justifyContent: 'flex-end'}}>
                                    <button type="button" className="btn btn-secondary" data-dismiss="modal" style={{marginRight: '5px'}}>Close</button>
                                    <button onClick={()=>{this.save()}} type="button" className="btn btn-success">Update</button>
                                </div>
                            </form> 
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}