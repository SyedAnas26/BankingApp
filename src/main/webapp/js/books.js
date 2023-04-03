$(document).ready(function () {
    addRowstoTable();
});


function AppViewModel(jsonArray) {
    var self = this;

    self.daysCount = {
        availableDays: ko.observableArray([10, 20, 30, 40])
    };

    self.query = ko.observable("");

    self.filter = {
        filterList: ko.observableArray(["All", "Available", "Not Available"]),
        selectedFilter: ko.observable("All")
    };

    self.bookList = ko.observableArray(ko.mapping.fromJS(jsonArray)());

    self.filteredBooks = ko.computed(function () {
        var search = self.query().toLowerCase();
        var selectedOption = self.filter.selectedFilter();
        return ko.utils.arrayFilter(self.bookList(), function (book) {
            if (selectedOption === "All" || selectedOption === "Not Available" && book.copies() <= 0 || selectedOption === "Available" && book.copies() > 0) {
                if (book.bookName().toLowerCase().indexOf(search) >= 0 || book.author().toLowerCase().indexOf(search) >= 0) {
                    return true;
                }
            }
            return false;
        });
    });

    self.isLoading = ko.observable(false);

    self.rentBook = function (book) {

        self.isLoading(true);
        $.ajax({
            url: "rentBook",
            type: 'POST',
            data: {bookId: book.bookId(), userId: localStorage.getItem("userId"), noOfDays: book.rentDays()},
            success: function (response) {
                if (response.status === "1") {
                    book.copies(book.copies() - 1)
                    alert("Successfully Rented Book");
                } else {
                    alert("Something went wrong try again later !");
                }
                self.isLoading(false);
            }
        });
    }
}


function addRowstoTable() {
    $.ajax({
        url: "getBookList",
        type: 'GET',
        data: {userId: localStorage.getItem("userId")},
        success: function (response) {
            var bookArray = response;
            bookArray.forEach((book) => book['rentDays'] = 10);
            ko.applyBindings(new AppViewModel(bookArray));
        }
    });
}