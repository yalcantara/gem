

class ListTenantView extends React.Component {


    constructor(props) {
        super(props);
    }

    create(){
        this.refs.createDialog.show();
    }

    update(record){
        var arr = this.state.list;
        utils.findAndReplace(arr, 'id', record);
        utils.sortField(this.state.list, 'name');
        this.setState({list: arr});
    }

    edit(record){
        this.refs.editDialog.show(record);
    }

    remove(record){
        var c;
        c = "Are you sure you want to ";
        c += "<span style='font-weight: bold; color: red'>delete</span> ";
        c += "the tenant <span style='font-weight: bold;'>";
        c += record.name;
        c += "</span>.";

        var self = this;
        showConfirmDialog({content: c, title: 'Delete Tenant'}).then(()=>{
            rest.httpDelete('/rest/auth/tenants/ent/' + record.id).then(()=>{
                self.props.deleteHandler(record);
            });
        });
    }

    componentDidMount(){
        this.props.mountHandler(this.props.match);
    }

    render() {
        var self = this;
        const RenderRow = function (record) {
            return (
                <tr key={record.id}>
                    <td style={{padding: '0px'}}>
                        <div style={{flexDirection: 'row', display: 'flex',  justifyContent: 'center', marginTop: '4px'}}>
                            <i onClick={()=>{self.edit(record)}} className="silk silk-pencil" style={{cursor: 'pointer', marginRight: '10px'}}></i>
                            <i onClick={()=>{self.remove(record)}} className="silk silk-cross" style={{cursor: 'pointer'}}></i>
                        </div>
                    </td>
                    <td style={{textAlign: 'right'}}>{record.id}</td>
                    <td style={{paddingLeft: '15px', paddingRight: '15px'}}>
                        <ReactRouterDOM.Link to={'/tenants/' + record.name +'/realms'}>{record.name}</ReactRouterDOM.Link>
                    </td>
                    <td>{record.label}</td>
                    <td style={{textAlign: 'right'}}>{utils.fromNow(record.lastModifiedDate)}</td>
                    <td style={{textAlign: 'right'}}>{utils.formatDate(record.createdDate)}</td>
                </tr>
            );
        };

        return (
            <div>
                <NewTenantDialog ref="createDialog" createHandler={this.props.createHandler}/>
                <EditTenantDialog ref="editDialog" selected={this.props.selected} updateHandler={this.props.updateHandler}/>
                <div style={{marginBottom: '10px'}}>
                    <div style={{display: 'inline-block'}}>
                        <Nav match={this.props.match}/>
                    </div>
                    <div style={{display: 'inline-block', float: 'right'}}>
                        <a href="#" onClick={(event)=>{this.create()}}>
                            <i className="fas fa-plus-circle" style={{marginRight: '5px'}}></i>
                            Create
                        </a>
                    </div>
                </div>
                <table className="table table-striped table-bordered table-hover table-sm">
                    <thead>
                        <tr>
                            <td style={{width: '60px'}}></td>
                            <td>ID</td>
                            <td>Name</td>
                            <td>Label</td>
                            <td style={{minWidth: '160px'}}>Last Modified Date</td>
                            <td style={{minWidth: '290px'}}>Created Date</td>
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