$(document).ready(function () {
    addRowstoTable();
});

function AppViewModel(jsonArray) {
    var self = this;
    self.query = ko.observable("");
    self.userList = ko.observableArray(ko.mapping.fromJS(jsonArray)());
    self.filteredUsers = ko.computed(function () {
        var search = self.query().toLowerCase();
        return ko.utils.arrayFilter(self.userList(), function (user) {
            return user.name().toLowerCase().indexOf(search) >= 0 || user.email().toLowerCase().indexOf(search) >= 0;
        });
    });
}

function addRowstoTable() {
    $.ajax({
        url: "getUserList",
        type: 'GET',
        data: {userId: localStorage.getItem("userId")},
        success: function (response) {
            ko.applyBindings(new AppViewModel(response));
        }
    });
} 