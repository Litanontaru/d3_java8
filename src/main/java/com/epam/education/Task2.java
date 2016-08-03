package com.epam.education;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

/**
 * @author Andrei_Yakushin
 * @since 8/3/2016 11:50 AM
 */
public class Task2 {
    public static void main(String[] args) {
        System.out.println("SEQUENTIAL");
        perform(StreamMaker.sequential());

        System.out.println("\nPARALLEL");
        perform(StreamMaker.parallel());
    }

    private static void perform(StreamMaker maker) {
        Book lotr = new Book("The Lord of the Rings", 600);
        Book hobbit = new Book("The Hobbit", 80);
        Book web = new Book("Charlotte's Web", 350);
        Book expected = new Book("What to Expect When You're Expecting", 250);
        Book littlePrince = new Book("The Little Prince", 220);
        Book harryPotter1 = new Book("Harry Potter and the Philosopher's Stone", 150);
        Book none = new Book("And Then There Were None", 180);

        Author tolkien = new Author("J. R. R. Tolkien", (short) 124).wroteBooks(lotr, hobbit);
        Author white = new Author("E.B. White", (short) 117).wroteBooks(web);
        Author williams = new Author("Garth Williams", (short) 104).wroteBooks(web);
        Author eisenberg = new Author("Arlene Eisenberg", (short) 81).wroteBooks(expected);
        Author murkoff = new Author("Heidi Murkoff", (short) 44).wroteBooks(expected);
        Author exupéry = new Author("Antoine de Saint-Exupéry", (short) 44).wroteBooks(littlePrince);
        Author rowling = new Author("J.K. Rowling", (short) 44).wroteBooks(harryPotter1);
        Author christie = new Author("Agatha Christie", (short) 44).wroteBooks(none);
        Author yongTolkien = new Author("J. R. R. Tolkien", (short) 7);

        Book[] books = new Book[]{lotr, hobbit, web, expected, littlePrince, harryPotter1, none};
        Author[] authors = new Author[]{tolkien, white, williams, eisenberg, murkoff, exupéry, rowling, christie, yongTolkien};

        System.out.println("Some have more than 200 pages: " + maker.stream(books).filter(book -> book.getNumberOfPages() > 200).findFirst().isPresent());
        System.out.println("All have more than 200 pages: " + !maker.stream(books).filter(book -> book.getNumberOfPages() <= 200).findFirst().isPresent());

        String min = maker.stream(books)
                .min((one, another) -> Integer.compare(one.getNumberOfPages(), another.getNumberOfPages()))
                .map(Book::getTitle)
                .orElse("-");
        System.out.println("Minimum pages: " + min);

        String max = maker.stream(books)
                .max((one, another) -> Integer.compare(one.getNumberOfPages(), another.getNumberOfPages()))
                .map(Book::getTitle)
                .orElse("-");
        System.out.println("Maximum pages: " + max);

        Comparator<Book> comparator = (a, b) -> Integer.compare(a.getNumberOfPages(), b.getNumberOfPages());
        comparator = comparator.thenComparing((a, b) -> a.getTitle().compareTo(b.getTitle()));
        System.out.println("\n== Books with one Author, sorted by pages/title ==");
        maker.stream(books)
                .filter(book -> book.getAuthors().size() == 1)
                .sorted(comparator)
                .map(Book::getTitle)
                .forEachOrdered(System.out::println);  //because of parallel sorted

        System.out.println("\n== Distinct Authors ==");
        maker.stream(authors)
                .peek(author -> {
                    //add debug code here
                })
                .distinct()
                .peek(author -> {
                    //add debug code here
                })
                .map(Author::getName)
                .peek(name -> {
                    //add debug code here
                })
                .forEach(System.out::println);
    }


    //------------------------------------------------------------------------------------------------------------------

    private static class Author {
        private final String name;
        private final short age;
        private final List<Book> books = new ArrayList<>();

        public Author(String name, short age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public short getAge() {
            return age;
        }

        public List<Book> getBooks() {
            return books;
        }

        public Author wroteBooks(Book... books) {
            for (Book book : books) {
                this.books.add(book);
                book.getAuthors().add(this);
            }
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Author author = (Author) o;
            return name.equals(author.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    private static class Book {
        private final String title;
        private final List<Author> authors = new ArrayList<>();
        private final int numberOfPages;

        public Book(String title, int numberOfPages) {
            this.title = title;
            this.numberOfPages = numberOfPages;
        }

        public String getTitle() {
            return title;
        }

        public List<Author> getAuthors() {
            return authors;
        }

        public int getNumberOfPages() {
            return numberOfPages;
        }
    }

    private interface StreamMaker {
        <T> Stream<T> stream(T[] array);

        static StreamMaker sequential() {
            return new StreamMaker() {
                @Override
                public <T> Stream<T> stream(T[] array) {
                    return Arrays.stream(array);
                }
            };
        }

        static StreamMaker parallel() {
            return new StreamMaker() {
                @Override
                public <T> Stream<T> stream(T[] array) {
                    return Arrays.stream(array).parallel();
                }
            };
        }
    }
}
