
class ListKeyView extends React.Component{

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
        c += "the key <span style='font-weight: bold;'>";
        c += record.name;
        c += "</span>.";

        var self = this;
        showConfirmDialog({content: c, title: 'Delete Key'}).then(()=>{
            var app = self.props.crtApp.name;
            var prop = self.props.crtProp.name;

            var url;
            url = '/rest/config/apps/' + app + '/properties/' + prop +'/keys/';
            url += record.name;

            rest.httpDelete(url).then(()=>{
                self.props.deleteHandler(record);
            });
        });
    }

    componentDidMount(){
        this.props.mountHandler(this.props.match);
    }

    render(){
        var self = this;
        const RenderRow = function (record) {
            return (
                <tr key={record._id}>
                    <td style={{padding: '0px'}}>
                        <div style={{flexDirection: 'row', display: 'flex',  justifyContent: 'center', marginTop: '4px'}}>
                            <i onClick={()=>{self.edit(record);return false}} className="silk silk-pencil" style={{cursor: 'pointer', marginRight: '10px'}}></i>
                            <i onClick={()=>{self.remove(record)}} className="silk silk-cross" style={{cursor: 'pointer'}}></i>
                        </div>
                    </td>
                    <td>{record.name}</td>
                    <td>{record.value}</td>
                    <td style={{textAlign: 'right'}}>{moment(new Date(record.lastUpdate)).fromNow()}</td>
                    <td style={{textAlign: 'right'}}>{utils.formatDate(record.creationDate)}</td>
                </tr>
            );
        };

        return (
            <div>
                <NewKeyDialog ref="createDialog"
                    crtApp={this.props.crtApp}
                    crtProp={this.props.crtProp}
                    createHandler={this.props.createHandler}/>

                <EditKeyDialog ref="editDialog"
                    crtApp={this.props.crtApp}
                    crtProp={this.props.crtProp}
                    updateHandler={this.props.updateHandler}/>
                    
                <div style={{ display: 'flex', justifyContent: 'flex-start' }}>
                    <a href="#" onClick={(e)=>{e.preventDefault(); this.create();}}>
                        <i className="fas fa-plus-circle" style={{marginRight: '5px'}}></i>
                        Create
                    </a>
                </div>
                <table className="table table-striped table-bordered table-hover table-sm">
                    <thead>
                        <tr>
                            <td style={{width: '60px'}}></td>
                            <td>Name</td>
                            <td>Value</td>
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