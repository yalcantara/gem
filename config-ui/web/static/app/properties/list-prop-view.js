
class ListPropView extends React.Component{

    constructor(props){
        super(props);
    }

    create(){
        this.refs.createDialog.show();
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
                    <td style={{paddingLeft: '15px', paddingRight: '15px'}}>
                        <ReactRouterDOM.Link to={'/apps/' + self.props.crtApp.name +'/' +record.name}>{record.name}</ReactRouterDOM.Link>
                    </td>
                    <td>{record.label}</td>
                    <td style={{textAlign: 'right'}}>{moment(new Date(record.lastUpdate)).fromNow()}</td>
                    <td style={{textAlign: 'right'}}>{utils.formatDate(record.creationDate)}</td>
                </tr>
            );
        };

        return (
            <div>
                <NewPropDialog ref="createDialog" crtApp={this.props.crtApp} createHandler={this.props.createHandler}/>
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
                            <td>Label</td>
                            <td style={{minWidth: '140px'}}>Last Update</td>
                            <td style={{minWidth: '250px'}}>Creation Date</td>
                        </tr>
                    </thead>
                    <tbody>
                        {this.props.crtApp.$properties.map(RenderRow)}
                    </tbody>
                </table>
            </div>
        );
    }
}