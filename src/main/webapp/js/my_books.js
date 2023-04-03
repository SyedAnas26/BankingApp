$(document).ready(function () {
    addRowstoTable();
});


function AppViewModel(jsonArray) {
    var self = this;

    self.query = ko.observable("");
    self.isLoading = ko.observable(false);

    self.filter = {
        filterList: ko.observableArray(["All", "Issued", "Pending Issue", "Waiting for Renewal", "Renewed"]),
        selectedFilter: ko.observable("All")
    };

    self.bookList = ko.observableArray(ko.mapping.fromJS(jsonArray)());

    self.filteredBooks = ko.computed(function () {
        var search = self.query().toLowerCase();
        var selectedOption = self.filter.selectedFilter();
        return ko.utils.arrayFilter(self.bookList(), function (book) {
            if (selectedOption === "All" || selectedOption === book.status()) {
                if (book.bookName().toLowerCase().indexOf(search) >= 0 || book.author().toLowerCase().indexOf(search) >= 0) {
                    return true;
                }
            }
            return false;
        });
    });

    self.getStatusColor = function (book) {
        if(book.status() === "Pending Issue"){
            return "orange";
        }else if (book.status() === "Issued"){
            return "green"
        }else{
            return "darkblue"
        }
    }


    self.deleteBook = function (book) {
        if (!self.isLoading()) {
            self.isLoading(true);
            $.ajax({
                url: "deleteRentedBook",
                type: 'DELETE',
                data: JSON.stringify({
                    rentedBookId: book.rentedBookId(),
                    userId: localStorage.getItem("userId"),
                    bookId: book.bookId()
                }),
                success: function (response) {
                    if (response.status === "1") {
                        self.bookList.remove(book)
                        alert("Successfully Deleted Rented Book");
                        self.isLoading(false);
                    } else {
                        alert("Something went wrong try again later !");
                        self.isLoading(false);
                    }
                }
            });
        }
    }
}


function addRowstoTable() {
    $.ajax({
        url: "getUserBooks",
        type: 'GET',
        data: {userId: localStorage.getItem("userId"), email: localStorage.getItem("email")},
        success: function (response) {
            ko.applyBindings(new AppViewModel(response.userBookList));
        }
    });
}