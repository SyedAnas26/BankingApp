
function BankStatementViewModel() {
    var self = this;

    // Transaction properties
    self.transactions = ko.observableArray([]);
    self.filteredTransactions = ko.observableArray([]);
    self.currentPageIndex = ko.observable(0);
    self.itemsPerPage = ko.observable(20);
    self.transactionTypeFilter = ko.observable("");
    self.modeOfPaymentFilter = ko.observable("");
    self.transactionStatusFilter = ko.observable("");
    self.startDateFilter = ko.observable("");
    self.endDateFilter = ko.observable("");

    // Pagination properties
    self.totalPages = ko.computed(function() {
        return Math.ceil(self.filteredTransactions().length / self.itemsPerPage());
    });
    self.paginationRange = ko.computed(function() {
        var range = [];
        for (var i = 0; i < self.totalPages(); i++) {
            range.push(i);
        }
        return range;
    });

    // Filtered transactions computed property
    self.filteredTransactions = ko.computed(function() {
        var filtered = self.transactions();
        if (self.transactionTypeFilter() !== "") {
            filtered = filtered.filter(function(item) {
                return item.transactionType === parseInt(self.transactionTypeFilter());
            });
        }
        if (self.modeOfPaymentFilter() !== "") {
            filtered = filtered.filter(function(item) {
                return item.modeOfPayment === parseInt(self.modeOfPaymentFilter());
            });
        }
        if (self.transactionStatusFilter() !== "") {
            filtered = filtered.filter(function(item) {
                return item.transactionStatus === parseInt(self.transactionStatusFilter());
            });
        }
        if (self.startDateFilter() !== "" && self.endDateFilter() !== "") {
            var startDate = moment(self.startDateFilter(), "YYYY-MM-DD");
            var endDate = moment(self.endDateFilter(), "YYYY-MM-DD");
            filtered = filtered.filter(function(item) {
                var transactionDate = moment(item.createdDate);
                return transactionDate.isBetween(startDate, endDate, null, "[]");
            });
        }
        return filtered;
    });

    // Go to the previous page of transactions
    self.goToPreviousPage = function() {
        if (self.currentPageIndex() > 0) {
            self.currentPageIndex(self.currentPageIndex() - 1);
        }
    };

    // Go to the next page of transactions
    self.goToNextPage = function() {
        if (self.currentPageIndex() < (self.totalPages() - 1)) {
            self.currentPageIndex(self.currentPageIndex() + 1);
        }
    };

    // Go to a specific page of transactions
    self.goToPage = function(pageIndex) {
        self.currentPageIndex(pageIndex);
    };

    // Get the name of the transaction type
    self.getTransactionType = function(typeId) {
        switch(typeId) {
            case 1:
                return "Credit";
            case 2:
                return "Debit";
            default:
                return "";
        }
    };

    // Get the name of the mode of payment
    self.getModeOfPayment = function(paymentId) {
        switch(paymentId) {
            case 1:
                return "UPI";
            case 2:
                return "Bank Transfer";
            case 3:
                return "ATM";
            default:
                return "";
        }
    };

    // Get the name of the transaction status
    self.getTransactionStatus = function(statusId) {
        switch(statusId) {
            case 1:
                return "Processing";
            case 2:
                return "Success";
            case 3:
                return "Failed";
            default:
                return "";
        }
    };

    // Export bank statements as PDF
    self.exportAsPDF = function() {
        var doc = new jsPDF().autoTable({
            head: [["Account Number", "Created Date", "Mode of Payment", "Transaction Type", "Amount", "Transaction Status"]],
            body: self.filteredTransactions().map(function(transaction) {
                return [transaction.accountId, moment(transaction.createdDate).format("DD MMMM YYYY, hh:mm:ss A"), self.getModeOfPayment(transaction.modeOfPayment), self.getTransactionType(transaction.transactionType), "\u20B9 " + transaction.amount, self.getTransactionStatus(transaction.transactionStatus)];
            })
        });
    };

    // Load transactions data from server
        $.ajax({
        url: "/api/customer/" + localStorage.getItem("userId") + "/transaction/list",
        type: 'GET',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: {userId: localStorage.getItem("userId")},
        success: function (data) {
            self.transactions(data);
        },
        error: function (jqXHR, exception) {
            var msg = '';
            if (jqXHR.status === 0) {
                msg = 'Not connect.\n Verify Network.';
            } else if (jqXHR.status === 401) {
                msg = 'UnAuthorized , Login Again';
            }
            alert(msg);
            location.href='/';
        }
        });
}

ko.applyBindings(new BankStatementViewModel());

