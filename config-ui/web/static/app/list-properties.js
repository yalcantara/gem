

class ListPropertyView extends React.Component {

    state = {
        list: [],
        app: null
    };

    props = {};

    constructor(props) {
        super(props);
        this.props = props;
        utils.sortField(this.state.list, 'name');
    }

    setList(list){
        this.setState({list: list});
    }

    setApp(app){
        this.setState({app: app});
    }

   


    remove(record){
        var c;
        c = "Are you sure you want to ";
        c += "<span style='font-weight: bold; color: red'>delete</span> ";
        c += "the property <span style='font-weight: bold;'>";
        c += record.name;
        c += "</span>.";

        var self = this;
        var app = this.state.app;
        showConfirmDialog({content: c, title: 'Delete Property'}).then(()=>{
            rest.httpDelete('/rest/config/apps/' + app +'/properties/' + record.name).then(()=>{
                var arr = utils.findAndDelete(self.state.list, 'name', record.name);
                self.setState({list: arr});
            });
        });
    }

    render() {
        var self = this;
        const RenderRow = function (record) {
            return (
                <tr key={record.id}>
                    <td style={{padding: '0px'}}>
                        <div style={{flexDirection: 'row', display: 'flex',  justifyContent: 'center'}}>
                            <i  className="silk silk-pencil" style={{cursor: 'pointer', marginRight: '10px'}}></i>
                            <i onClick={()=>{self.remove(record)}} className="silk silk-cross" style={{cursor: 'pointer'}}></i>
                        </div>
                    </td>
                    <td><a href="#">{record.name}</a></td>
                    <td>{record.label}</td>
                    <td style={{textAlign: 'right'}}>{moment(new Date(record.lastUpdate)).fromNow()}</td>
                    <td style={{textAlign: 'right'}}>{utils.formatDate(record.creationDate)}</td>
                </tr>
            );
        };

        return (
            <div>
                <div style={{ display: 'flex', justifyContent: 'flex-start' }}>
                    <a href="#" data-toggle="modal" data-target={'#'+ this.props.createModal}>
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
                            <td>Last Update</td>
                            <td>Creation Date</td>
                        </tr>
                    </thead>
                    <tbody>
                        {this.state.list.map(RenderRow)}
                    </tbody>
                </table>
            </div>
        );
    }
}