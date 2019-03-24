

class ListAppView extends React.Component {


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
        c += "the app <span style='font-weight: bold;'>";
        c += record.name;
        c += "</span>.";

        var self = this;
        showConfirmDialog({content: c, title: 'Delete App'}).then(()=>{
            rest.httpDelete('/rest/config/apps/' + record.name).then(()=>{
                self.props.deleteHandler(record);
            });
        });
    }

    render() {
        var self = this;
        const RenderRow = function (record) {
            return (
                <tr key={record._id}>
                    <td style={{padding: '0px'}}>
                        <div style={{flexDirection: 'row', display: 'flex',  justifyContent: 'center', marginTop: '4px'}}>
                            <i onClick={()=>{self.edit(record)}} className="silk silk-pencil" style={{cursor: 'pointer', marginRight: '10px'}}></i>
                            <i onClick={()=>{self.remove(record)}} className="silk silk-cross" style={{cursor: 'pointer'}}></i>
                        </div>
                    </td>
                    <td style={{paddingLeft: '15px', paddingRight: '15px'}}>
                        <ReactRouterDOM.Link to={'/apps/' + record.name +'/properties'}>{record.name}</ReactRouterDOM.Link>
                    </td>
                    <td>{record.label}</td>
                    <td style={{textAlign: 'right'}}>{moment(new Date(record.lastUpdate)).fromNow()}</td>
                    <td style={{textAlign: 'right'}}>{utils.formatDate(record.creationDate)}</td>
                </tr>
            );
        };

        return (
            <div>
                <NewAppDialog ref="createDialog" createHandler={this.props.createHandler}/>
                <EditAppDialog ref="editDialog" selected={this.props.selected} updateHandler={this.props.updateHandler}/>
                <div style={{ display: 'flex', justifyContent: 'flex-start' }}>
                    <a href="#" onClick={(event)=>{this.create()}}>
                        <i className="fas fa-plus-circle" style={{marginRight: '5px'}}></i>
                        Create
                    </a>
                </div>
                <table className="table table-striped table-bordered table-hover table-sm">
                    <thead>
                        <tr>
                            <td style={{width: '60px'}}></td>
                            <td>Name</td>
                            <td>Label</td>
                            <td style={{minWidth: '140px'}}>Last Update</td>
                            <td style={{minWidth: '250px'}}>Creation Date</td>
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