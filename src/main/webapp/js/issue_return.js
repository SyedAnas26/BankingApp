$(document).ready(function () {
    ko.applyBindings(new AppViewModel());
});


function AppViewModel() {
    var self = this;

    self.bookList = ko.observableArray([]);

    self.userName = ko.observable("");

    self.filter = {
        filterList: ko.observableArray(["All", "Pending Issue", "Issued", "Returned"]),
        selectedFilter: ko.observable("All")
    };

    self.isLoading = ko.observable(false);

    self.filteredBookList = ko.computed(function () {
        var selectedOption = self.filter.selectedFilter();
        return ko.utils.arrayFilter(self.bookList(), function (book) {
            return selectedOption === "All" || book.status() === selectedOption;
        });
    });

    self.initializeBookList = function () {
        self.isLoading(true);
        var email = document.getElementById("search").value;
        $.ajax({
            url: "getUserBooks",
            type: 'GET',
            data: {userId: localStorage.getItem("userId"), email: email},
            success: function (response) {
                self.userName(response.userName);
                self.bookList(ko.mapping.fromJS(response.userBookList)());
                self.isLoading(false);
            }
        });
    }

    self.changeStatus = function (book) {
        self.isLoading(true)
        var status = 0;
        var statusInWords = "";
        if (book.status() === "Pending Issue") {
            status = 1;
            statusInWords = "Issued"
        } else if (book.status() === "Issued") {
            status = 2
            statusInWords = "Returned"
        }
        $.ajax({
            url: "changeStatus",
            type: 'PUT',
            data: JSON.stringify({userId: localStorage.getItem("userId"), status: status, rentedBookId: book.rentedBookId()}),
            success: function (response) {
                if (response.status === "1") {
                    book.status(statusInWords)
                    alert("Status Changed successfully")
                } else {
                    alert("Something went wrong try again")
                }
                self.isLoading(false);
            }
        });
    }
}
