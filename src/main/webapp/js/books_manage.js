$(document).ready(function () {
    addRowstoTable();
});

function addRowstoTable() {
    $.ajax({
        url: "getBookList",
        type: 'GET',
        data: {userId: localStorage.getItem("userId")},
        success: function (response) {
            var bookArray = response;
            bookArray.forEach((book) => {
                    book['editing'] = false;
                    book['previousTotalCopies'] = book.totalCopies;
                }
            );
            ko.applyBindings(new AppViewModel(bookArray));
        }
    });
}


function AppViewModel(jsonArray) {
    var self = this;

    self.newBook = {
        bookId: ko.observable(0),
        bookName: ko.observable(""),
        author: ko.observable(""),
        publishedYear: ko.observable(0),
        totalCopies: ko.observable(0),
        price: ko.observable(0)
    }

    self.query = ko.observable("");

    self.bookList = ko.observableArray(ko.mapping.fromJS(jsonArray)());

    self.filteredBooks = ko.computed(function () {
        var search = self.query().toLowerCase();
        return ko.utils.arrayFilter(self.bookList(), function (book) {
            return book.bookName().toLowerCase().indexOf(search) >= 0 || book.author().toLowerCase().indexOf(search) >= 0;
        });
    });

    self.isLoading = ko.observable(false);

    self.edit = function (book, event) {
        book.editing(true);
        $(event.target).parents("tr").find(".add, .edit").toggle();
        $(".add-new").attr("disabled", "disabled");
    }

    self.save = function (book, event) {
        if (!self.isLoading()) {
            self.isLoading(true);
            var empty = false;
            var input = $(event.target).parents("tr").find('input');
            input.each(function () {
                var value = $(this).val()
                console.log(value);
                if (!value || value.trim() === "") {
                    $(this).addClass("error");
                    empty = true;
                } else {
                    $(this).removeClass("error");
                }
            });
            $(this).parents("tr").find(".error").first().focus();

            if (!empty && book) {
                if (book.totalCopies() < book.previousTotalCopies() - book.copies()) {
                    alert("Total copies cannot be lesser than the rented copies count");
                    return;
                }
                $.ajax({
                    url: "updateBook",
                    type: 'PUT',
                    data: JSON.stringify({book: ko.toJSON(book), userId: localStorage.getItem("userId")}),
                    success: function (response) {
                        if (response.status === "1") {
                            alert("Successfully Book Updated");
                            if (book.totalCopies() > book.previousTotalCopies()) {
                                book.copies((book.totalCopies() - book.previousTotalCopies()) + book.copies())
                            } else {
                                book.copies(book.totalCopies() - (book.previousTotalCopies() - book.copies()));
                            }
                            book.previousTotalCopies(book.totalCopies());
                        } else {
                            alert("Something went wrong try again later !");
                        }
                        book.editing(false);
                        $(".add-new").removeAttr("disabled");
                        self.isLoading(false)
                    }
                });
            } else {
                $.ajax({
                    url: "addBook",
                    type: 'POST',
                    data: {book: ko.toJSON(self.newBook), userId: localStorage.getItem("userId")},
                    success: function (response) {
                        if (response.status === "1") {
                            $(".add-new").removeAttr("disabled");
                            $(event.target).parents("tr").remove();
                            alert("Successfully Book Created");
                            location.reload();
                        } else {
                            alert("Something went wrong try again later !");
                        }
                        $(".add-new").removeAttr("disabled");
                        self.isLoading(false)
                    }
                });
            }
        }
    }

    self.deleteNew = function (data, event) {
        $(event.target).parents("tr").remove();
        $(".add-new").removeAttr("disabled");
    }

    self.addNew = function (data, event) {
        $(event.target).attr("disabled", "disabled");
        var row = '<tr id="newBook">' +
            '<td>Auto Generated</td>' +
            '<td><input type="text" data-bind="textInput: newBook.bookName" class="form-control" name="name" id="name"></td>' +
            '<td><input type="text" data-bind="textInput: newBook.author" class="form-control" name="authorName" id="authorName"></td>' +
            '<td><input type="number" data-bind="textInput: newBook.totalCopies" class="form-control" name="year" id="year"></td>' +
            '<td><input type="number" data-bind="textInput: newBook.publishedYear" class="form-control" name="copies" id="copies"></td>' +
            '<td><input type="number" data-bind="textInput: newBook.price" class="form-control" name="price" id="price"></td>' +
            ' <td><a class="add" data-bind="click : save" title="Add" data-toggle="tooltip"><i class="material-icons">playlist_add</i></a>' +
            ' <a class="delete" data-bind="click : deleteNew" title="Delete" data-toggle="tooltip"><i class="material-icons">delete</i></a></td>'
        '</tr>';
        $("table").append(row);
        ko.applyBindings(AppViewModel, $("#newBook")[0]);
    }

    self.deleteBook = function (book) {
        if (!self.isLoading()) {
            self.isLoading(true)
            if (confirm("Are you sure want to delete the book? This will delete all the linked RentalBook Entries of this book")) {
                $.ajax({
                    url: "deleteBook",
                    type: 'DELETE',
                    data: JSON.stringify({bookId: book.bookId(), userId: localStorage.getItem("userId")}),
                    success: function (response) {
                        if (response.status === "1") {
                            self.bookList.remove(book);
                            $(".add-new").removeAttr("disabled");
                        } else {
                            alert("Something went wrong try again later !");
                        }
                        self.isLoading(false)
                    }
                });
            }
        }
    }
}
