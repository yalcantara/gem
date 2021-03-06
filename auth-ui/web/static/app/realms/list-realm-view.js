
class ListRealmView extends React.Component{

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
        c += "the realm <span style='font-weight: bold;'>";
        c += record.name;
        c += "</span>.";

        var self = this;
        showConfirmDialog({content: c, title: 'Delete Property'}).then(()=>{
            var tenant = self.props.crtTenant.id;
            rest.httpDelete('/rest/auth/tenants/ent/' + tenant + '/realms/' + record.id).then(()=>{
                self.props.deleteHandler(record);
            });
        });
    }

    componentDidMount(){
        this.props.mountHandler(this.props.match);
    }

    render(){
        var self = this;
        var tenant = self.props.crtTenant.name;
        const RenderRow = function (record) {
            return (
                <tr key={record._id}>
                    <td style={{padding: '0px'}}>
                        <div style={{flexDirection: 'row', display: 'flex',  justifyContent: 'center', marginTop: '4px'}}>
                            <i onClick={()=>{self.edit(record);return false}} className="silk silk-pencil" style={{cursor: 'pointer', marginRight: '10px'}}></i>
                            <i onClick={()=>{self.remove(record)}} className="silk silk-cross" style={{cursor: 'pointer'}}></i>
                        </div>
                    </td>
                    <td>{record.id}</td>
                    <td style={{paddingLeft: '15px', paddingRight: '15px', wordBreak: 'break-all'}}>
                        <ReactRouterDOM.Link to={'/tenants/' + tenant +'/realms/' +record.name + '/keys'}>{record.name}</ReactRouterDOM.Link>
                    </td>
                    <td style={{wordBreak: 'break-all'}}>{record.label}</td>
                    <td style={{textAlign: 'right'}}>{moment(new Date(record.lastUpdate)).fromNow()}</td>
                    <td style={{textAlign: 'right'}}>{utils.formatDate(record.creationDate)}</td>
                </tr>
            );
        };

        return (
            <div>
                <NewRealmDialog ref="createDialog" crtTenant={this.props.crtTenant} createHandler={this.props.createHandler}/>
                <EditRealmDialog ref="editDialog" crtTenant={this.props.crtTenant} updateHandler={this.props.updateHandler}/>
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
                            <td>ID</td>
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