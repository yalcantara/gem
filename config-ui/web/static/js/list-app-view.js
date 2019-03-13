

class ListAppView extends React.Component {

    state = {
        apps: []
    };

    props = {};

    constructor(props) {
        super(props);
        this.props = props;
        this.state.apps = props.apps.slice(0);
        utils.sortField(this.state.apps, 'name');
    }
    render() {
        const RenderRow = function (row) {
            return (
                <tr key={row._id.$oid}>
                    <td >
                        <div style={{flexDirection: 'row', display: 'flex'}}>

                        <div  style={{backgroundImage: "url('/static/icons/16x16/silk/pencil.png')", width: '16px', height: '16px', marginRight: '10px'}}></div>
                        <div  style={{backgroundImage: "url('/static/icons/16x16/silk/cross.png')", width: '16px', height: '16px'}}></div>
                        </div>
                    </td>
                    <td><a href="#">{row.name}</a></td>
                    <td>{row.label}</td>
                    <td>{moment(new Date(row.lastUpdate)).fromNow()}</td>
                    <td style={{textAlign: 'right'}}>{utils.formatDate(row.creationDate)}</td>
                </tr>
            );
        };

        return (
            <div>
                <div style={{ display: 'flex', justifyContent: 'flex-start' }}>
                    <a href="#" data-toggle="modal" data-target={'#'+ this.props.createModal}>
                        <i className="fas fa-plus-circle"></i>
                        Create
                    </a>
                </div>
                <table className="table table-striped table-bordered table-hover table-md">
                    <thead>
                        <tr>
                            <td></td>
                            <td>Name</td>
                            <td>Label</td>
                            <td>Last Update</td>
                            <td>Creation Date</td>
                        </tr>
                    </thead>
                    <tbody>
                        {this.state.apps.map(RenderRow)}
                    </tbody>
                </table>
            </div>
        );
    }
}