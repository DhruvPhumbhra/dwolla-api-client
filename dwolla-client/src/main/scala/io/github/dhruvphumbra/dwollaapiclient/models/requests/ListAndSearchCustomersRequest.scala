package io.github.dhruvphumbra.dwollaapiclient.models.requests

case class ListAndSearchCustomersRequest(
                                          limit: Option[Int] = None,
                                          offset: Option[Int] = None,
                                          search: Option[String] = None,
                                          email: Option[String] = None,
                                          status: Option[String] = None
                                        )
