
class ListPropView extends React.Component{

    constructor(props){
        super(props);
    }

    create(){
        this.refs.createDialog.show();
    }

    edit(record){
        this.refs.editDialog.show(record);
    }

    remove(record){
        var c;
        c = "Are you sure you want to ";
        c += "<span style='font-weight: bold; color: red'>delete</span> ";
        c += "the property <span style='font-weight: bold;'>";
        c += record.name;
        c += "</span>.";

        var self = this;
        showConfirmDialog({content: c, title: 'Delete Property'}).then(()=>{
            var app = self.props.crtApp.name;
            rest.httpDelete('/rest/config/apps/' + app + '/properties/' + record.name).then(()=>{
                self.props.deleteHandler(record);
            });
        });
    }

    componentDidMount(){
        this.props.mountHandler(this.props.match);
    }

    render(){
        var self = this;
        var app = self.props.crtApp.name;
        const RenderRow = function (record) {
            return (
                <tr key={record._id}>
                    <td style={{padding: '0px'}}>
                        <div style={{flexDirection: 'row', display: 'flex',  justifyContent: 'center', marginTop: '4px'}}>
                            <i onClick={()=>{self.edit(record);return false}} className="silk silk-pencil" style={{cursor: 'pointer', marginRight: '10px'}}></i>
                            <i onClick={()=>{self.remove(record)}} className="silk silk-cross" style={{cursor: 'pointer'}}></i>
                        </div>
                    </td>
                    <td style={{paddingLeft: '15px', paddingRight: '15px', wordBreak: 'break-all'}}>
                        <ReactRouterDOM.Link to={'/apps/' + app +'/properties/' +record.name + '/keys'}>{record.name}</ReactRouterDOM.Link>
                    </td>
                    <td style={{wordBreak: 'break-all'}}>{record.label}</td>
                    <td style={{textAlign: 'right'}}>{moment(new Date(record.lastUpdate)).fromNow()}</td>
                    <td style={{textAlign: 'right'}}>{utils.formatDate(record.creationDate)}</td>
                </tr>
            );
        };

        return (
            <div>
                <NewPropDialog ref="createDialog" crtApp={this.props.crtApp} createHandler={this.props.createHandler}/>
                <EditPropDialog ref="editDialog" crtApp={this.props.crtApp} updateHandler={this.props.updateHandler}/>
                <div style={{marginBottom: '10px'}}>
                    <div style={{display: 'inline-block'}}>
                        <Nav match={this.props.match}/>
                    </div>
                    <div style={{display: 'inline-block', float: 'right'}}>
                        <a href="#" onClick={(e)=>{e.preventDefault(); this.create();}}>
                            <i className="fas fa-plus-circle" style={{marginRight: '5px'}}></i>
                            Create
                        </a>
                    </div>
                </div>
                <table className="table table-striped table-bordered table-hover table-sm">
                    <thead>
                        <tr>
                            <td style={{width: '60px'}}></td>
                            <td>Name</td>
                            <td>Label</td>
                            <td style={{minWidth: '160px'}}>Last Update</td>
                            <td style={{minWidth: '290px'}}>Creation Date</td>
                        </tr>
                    </thead>
                    <tbody>
                        {this.props.list.map(RenderRow)}
                    </tbody>
                </table>
            </div>
        );
    }
}